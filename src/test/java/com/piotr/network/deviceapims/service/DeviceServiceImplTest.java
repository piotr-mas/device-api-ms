package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.exception.InvalidRequestException;
import com.piotr.network.deviceapims.generated.model.*;
import com.piotr.network.deviceapims.mapper.DeviceMapper;
import com.piotr.network.deviceapims.repository.DeviceRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    DeviceRepository deviceRepository;
    @Mock
    DeviceMapper deviceMapper;
    @InjectMocks
    DeviceServiceImpl service;

    private final String childMacAddress =  "00:1A:2B:3C:4D:5E";
    private final String parentMacAddress = "60:7A:8B:9C:4D:5E";

    @Test
    void whenRegisterDevice_thenReturnRegisterDeviceResponse() {
        var child = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getMacAddress), childMacAddress)
                .set(field(RegisterDeviceRequest::getUplinkMacAddress), parentMacAddress)
                .create();
        var parent = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getMacAddress), parentMacAddress)
                .setBlank(field(RegisterDeviceRequest::getUplinkMacAddress))
                .create();
        var parentEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getDeviceType), parent.getDeviceType())
                .set(field(DeviceEntity::getMacAddress), parent.getMacAddress())
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .set(field(DeviceEntity::getId), UUID.randomUUID())
                .create();
        var childEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getDeviceType), parent.getDeviceType())
                .set(field(DeviceEntity::getMacAddress), parent.getMacAddress())
                .set(field(DeviceEntity::getUplinkDevice), parentEntity)
                .set(field(DeviceEntity::getId), UUID.randomUUID())
                .create();
        var response = Instancio.of(RegisterDeviceResponse.class)
                .set(field(RegisterDeviceResponse::getDeviceType),  child.getDeviceType())
                .set(field(RegisterDeviceResponse::getMacAddress),  child.getMacAddress())
                .set(field(RegisterDeviceResponse::getUplinkMacAddress),  parent.getMacAddress())
                .set(field(RegisterDeviceResponse::getId),  childEntity.getId())
                .create();
        //when
        when(deviceMapper.mapDeviceDtoToDeviceEntity(any(RegisterDeviceRequest.class)))
                .thenReturn(childEntity);
        when(deviceRepository.findByMacAddress(anyString()))
                .thenReturn(Optional.ofNullable(parentEntity));
        when(deviceRepository.save(any(DeviceEntity.class)))
                .thenReturn(childEntity);
        when(deviceMapper.mapDeviceEntityToRegisterDeviceResponse(any(DeviceEntity.class)))
                .thenReturn(response);
        //call method
        var result = service.registerDevice(child);
        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getDeviceType(), result.getDeviceType());
        assertEquals(response.getMacAddress(), result.getMacAddress());
        assertEquals(response.getUplinkMacAddress() , result.getUplinkMacAddress());
    }

    @Test
    void whenRegisterDevice_thenThrowInvalidRequestException() {
        var child = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getMacAddress), childMacAddress)
                .set(field(RegisterDeviceRequest::getUplinkMacAddress), parentMacAddress)
                .create();

        var childEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), childMacAddress)
                .create();
        //when
        when(deviceMapper.mapDeviceDtoToDeviceEntity(any(RegisterDeviceRequest.class)))
                .thenReturn(childEntity);
        //call method
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> service.registerDevice(child));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Parent device with MAC "+parentMacAddress + " not found", exception.getMessage());
    }

    @Test
    void whenGetDevices_thanReturnListOfDevicesSortedByDeviceType() {//Device order: gateway -> switch -> access point
        AtomicInteger counter = new AtomicInteger(0);
        var allDevicesEntities = Instancio.ofList(DeviceEntity.class)
                .size(5)
                .supply(field(DeviceEntity::getDeviceType), () -> switch (counter.getAndIncrement() % 5) {
                    case 1, 3 -> DeviceType.GATEWAY;
                    case 2, 4 -> DeviceType.SWITCH;
                    default -> DeviceType.ACCESS_POINT;
                })
                .create();
        //when
        when(deviceRepository.findAll())
                .thenReturn(allDevicesEntities);
        when(deviceMapper.mapDeviceEntityToDeviceResponse(any(DeviceEntity.class)))
                .thenAnswer(invocation -> {
                    DeviceEntity deviceEntity = invocation.getArgument(0);
                    return Instancio.of(DeviceResponse.class)
                            .set(field(DeviceResponse::getDeviceType),  deviceEntity.getDeviceType())
                            .create();
                        });

        //call method
        var result = service.getDevices();
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(DeviceType.GATEWAY, result.get(0).getDeviceType());
        assertEquals(DeviceType.SWITCH, result.get(2).getDeviceType());
        assertEquals(DeviceType.ACCESS_POINT, result.get(4).getDeviceType());
    }

    @Test
    void whenGetDevices_thanReturnInvalidRequestException_NoDevicesFound() {
        //call method
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> service.getDevices());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No device(s) found", exception.getMessage());
    }

    @Test
    void whenGetDeviceByMac_thanReturnDeviceResponse() {
        var parentEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                .setBlank(field(DeviceEntity::getUplinkDevice))
                .set(field(DeviceEntity::getId), UUID.randomUUID())
                .create();
        //when
        when(deviceRepository.findByMacAddress(anyString()))
                .thenReturn(Optional.ofNullable(parentEntity));
        when(deviceMapper.mapDeviceEntityToDeviceResponse(any(DeviceEntity.class)))
                .thenAnswer(invocation -> {
                    DeviceEntity deviceEntity = invocation.getArgument(0);
                    return Instancio.of(DeviceResponse.class)
                            .set(field(DeviceResponse::getDeviceType),  deviceEntity.getDeviceType())
                            .set(field(DeviceResponse::getMacAddress), deviceEntity.getMacAddress())
                            .create();
                });
        //call method
        var result = service.getDeviceByMac(parentMacAddress);
        assertNotNull(result);
        assertNotNull(parentEntity);
        assertEquals(parentEntity.getDeviceType(), result.getDeviceType());
        assertEquals(parentEntity.getMacAddress(), result.getMacAddress());
    }

    @Test
    void whenGetDeviceByMac_thanReturnInvalidRequestException_DeviceNotFound() {
        //call method
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> service.getDeviceByMac(parentMacAddress));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Device with MAC "+parentMacAddress + " not found", exception.getMessage());
    }

    @Test
    void whenGetTopologyNodes_thanReturnListOfDevicesAsTreeStructure() {
        var allRootDevicesEntities = Instancio.ofList(DeviceEntity.class)
                .size(3)
                .set(field(DeviceEntity::getDownlinkDevices), Instancio.ofList(DeviceEntity.class)
                        .size(1)
                        .set(field(DeviceEntity::getDownlinkDevices), Instancio.ofList(DeviceEntity.class)
                                .size(1)
                                .create())
                        .create())
                .create();
        //when
        when(deviceRepository.findByUplinkDeviceIsNull())
                .thenReturn(allRootDevicesEntities);
        when(deviceMapper.mapEntityToTopologyNodeResponse(any(DeviceEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    DeviceEntity deviceEntity = invocationOnMock.getArgument(0);
                    return Instancio.of(TopologyNodeResponse.class)
                            .set(field(TopologyNodeResponse::getMacAddress), deviceEntity.getMacAddress())
                            .set(field(TopologyNodeResponse::getChildren), deviceEntity.getDownlinkDevices())
                            .create();
                });
        //call method
        var result = service.getTopologyNodes();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void whenGetTopologyNodes_thanReturnInvalidRequestException_NoDevicesFound() {
        //call method
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> service.getTopologyNodes());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No device(s) found in network topology", exception.getMessage());
    }

    @Test
    void whenGetTopologyNodeByMac_thanReturnTopologyNodeResponse() {
        final String grandChild = "80:7A:6B:5C:4D:5E";
        var parentDeviceEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                .set(field(DeviceEntity::getDownlinkDevices), Instancio.ofList(DeviceEntity.class)
                        .set(field(DeviceEntity::getMacAddress), childMacAddress)
                        .set(field(DeviceEntity::getDownlinkDevices), Instancio.ofList(DeviceEntity.class)
                                .set(field(DeviceEntity::getMacAddress), grandChild)
                                .create())
                        .create())
                .create();
        //when
        when(deviceRepository.findByMacAddress(anyString()))
                .thenReturn(Optional.ofNullable(parentDeviceEntity));
        when(deviceMapper.mapEntityToTopologyNodeResponse(any(DeviceEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    DeviceEntity deviceEntity = invocationOnMock.getArgument(0);
                    return Instancio.of(TopologyNodeResponse.class)
                            .set(field(TopologyNodeResponse::getMacAddress), deviceEntity.getMacAddress())
                            .set(field(TopologyNodeResponse::getChildren), deviceEntity.getDownlinkDevices())
                            .create();
                });
        //call method
        var result = service.getTopologyNodeByMac(parentMacAddress);
        assertNotNull(result);
        assertEquals(parentMacAddress, result.getMacAddress());
        assertEquals(childMacAddress, result.getChildren().get(0).getMacAddress());
        assertEquals(grandChild, result.getChildren().get(0).getChildren().get(0).getMacAddress());
    }

    @Test
    void whenGetTopologyNodebyMac_thanReturnInvalidRequestException_TopologyNotFound() {

        //call method
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> service.getTopologyNodeByMac(parentMacAddress));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Topology with device MAC "+parentMacAddress + " not found", exception.getMessage());
    }
}