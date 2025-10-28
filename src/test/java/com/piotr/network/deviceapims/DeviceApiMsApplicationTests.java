package com.piotr.network.deviceapims;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.generated.model.*;
import com.piotr.network.deviceapims.repository.DeviceRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for API controller
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DeviceApiMsApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setup() {
        // Deletes all entities in the table before running each @Test method
        deviceRepository.deleteAll();
    }

    @Test
    void testRegisterDevice_returns201() {
        var request = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getDeviceType), DeviceType.GATEWAY)
                .set(field(RegisterDeviceRequest::getMacAddress), "00:1A:2B:3C:4D:5E")
                .setBlank(field(RegisterDeviceRequest::getUplinkMacAddress))
                .create();
        var headers = Instancio.of(HttpHeaders.class).create();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        //call the API endpoint
        ResponseEntity<RegisterDeviceResponse> response = restTemplate.exchange("/devices",
                HttpMethod.POST, requestEntity, RegisterDeviceResponse.class);
        //assertion
        var body =  response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertNotNull(body);
        assertThat(body.getDeviceType()).isEqualTo(DeviceType.GATEWAY);
        assertThat(body.getMacAddress()).isEqualTo(request.getMacAddress());
        assertThat(body.getId()).isNotNull();
    }

    @Test
    void testRegisterDevice_returns400() {
        //request with random not valid UplinkMacAddress
        var request = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getDeviceType), DeviceType.GATEWAY)
                .set(field(RegisterDeviceRequest::getMacAddress), "00:1A:2B:3C:4D:5E")
                .create();
        var headers = Instancio.of(HttpHeaders.class).create();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        //call the API endpoint
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/devices",
                HttpMethod.POST, requestEntity, ErrorResponse.class);
        //assertion
        var body =  response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(body);
        assertThat(body.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getMessage()).contains("uplinkMacAddress: must match");
    }

    @Test
    void testGetDeviceByMac_returns200() {
        final String macAddress = "10:1A:2B:3C:4D:5E";
        var entity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), macAddress)
                .set(field(DeviceEntity::getDeviceType), DeviceType.GATEWAY)
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .setBlank(field(DeviceEntity::getId))
                .create();
        var persistedEntity = deviceRepository.save(entity);
        //call the API endpoint
        ResponseEntity<RegisterDeviceResponse> response = restTemplate.getForEntity("/devices/mac/"+macAddress,
                RegisterDeviceResponse.class, macAddress);
        //assertion
        var body =  response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(body);
        assertThat(body.getDeviceType()).isEqualTo(DeviceType.GATEWAY);
        assertThat(body.getMacAddress()).isEqualTo(macAddress);
        assertThat(body.getId()).isEqualTo(persistedEntity.getId());
    }

    @Test
    void testGetDeviceByMac_returns400() {
        //call the API endpoint
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/devices/mac/4444",
                HttpMethod.GET, HttpEntity.EMPTY, ErrorResponse.class);
        //assertion
        var body =  response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(body);
        assertThat(body.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getMessage()).contains("macAddress: must match");
    }

    @Test
    void testGetAllDevices_returns200() {
        final String macAddress = "70:1A:2B:3C:4D:5E";
        var entity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), macAddress)
                .set(field(DeviceEntity::getDeviceType), DeviceType.GATEWAY)
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .setBlank(field(DeviceEntity::getId))
                .create();
        var persistedEntity = deviceRepository.save(entity);
        //call the API endpoint
        ResponseEntity<List<RegisterDeviceResponse>> response = restTemplate.exchange("/devices", HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});
        //assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body =  response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertThat(body.get(0).getDeviceType().getValue()).isEqualTo(DeviceType.GATEWAY.getValue());
        assertThat(body.get(0).getMacAddress()).isEqualTo(macAddress);
        assertThat(body.get(0).getId()).isEqualTo(persistedEntity.getId());
    }

    @Test
    void testGetAllDevices_returns404() {
        //call the API endpoint
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/devices", HttpMethod.GET,
                HttpEntity.EMPTY, ErrorResponse.class);
        //assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        var body =  response.getBody();
        assertNotNull(body);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.getCode()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(body.getMessage()).contains("No device(s) found");
    }

    @Test
    void testDevicesTopologyMacAddressGet_returns200() {
        final String macAddress = "90:1A:2B:3C:4D:5E";
        var entity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), macAddress)
                .set(field(DeviceEntity::getDeviceType), DeviceType.GATEWAY)
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .setBlank(field(DeviceEntity::getId))
                .create();
        deviceRepository.save(entity);
        //call the API endpoint
        ResponseEntity<TopologyNodeResponse> response = restTemplate.exchange("/devices/topology/"+macAddress, HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});
        //assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body =  response.getBody();
        assertNotNull(body);
        assertThat(body.getChildren()).isEmpty();
        assertThat(body.getMacAddress() ).isEqualTo(macAddress);
    }

    @Test
    void testDevicesTopologyMacAddressGet_returns400() {
        //call the API endpoint
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/devices/topology/1234", HttpMethod.GET,
                HttpEntity.EMPTY, ErrorResponse.class);
        //assertion
        var body =  response.getBody();
        assertNotNull(body);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getMessage()).contains("macAddress: must match");
    }

    @Test
    void testDevicesTopologyGetAll_returns200() {
        final String parentMacAddress = "80:1A:2B:3C:4D:5E";
        final String childMacAddress = "80:1A:2B:3C:4D:5F";
        //create parent and child devices
        var parentEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                .set(field(DeviceEntity::getDeviceType), DeviceType.GATEWAY)
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .setBlank(field(DeviceEntity::getId))
                .create();
        var savedParent = deviceRepository.save(parentEntity);
        var childEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), childMacAddress)
                .set(field(DeviceEntity::getDeviceType), DeviceType.SWITCH)
                .set(field(DeviceEntity::getUplinkDevice), savedParent)
                .setBlank(field(DeviceEntity::getId))
                .create();
        deviceRepository.save(childEntity);

        //call the API endpoint
        ResponseEntity<List<TopologyNodeResponse>> response = restTemplate.exchange("/devices/topology", HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<>() {});
        //assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body =  response.getBody();
        assertNotNull(body);
        assertThat(body).hasSize(1);
        assertThat(body.get(0).getChildren()).hasSize(1);
        assertThat(body.get(0).getMacAddress()).isEqualTo(parentMacAddress);
        assertThat(body.get(0).getChildren().get(0).getMacAddress()).isEqualTo(childMacAddress);
    }

    @Test
    void testDevicesTopologyGet_returns404() {
        //call the API endpoint
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/devices/topology", HttpMethod.GET,
                HttpEntity.EMPTY, ErrorResponse.class);
        //assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        var body =  response.getBody();
        assertNotNull(body);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.getCode()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(body.getMessage()).contains("No device(s) found");
    }
}
