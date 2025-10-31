package com.piotr.network.deviceapims.mapper;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.generated.model.DeviceType;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeviceMapperTest {

    @InjectMocks
    private DeviceMapper deviceMapper;

    private final String childMacAddress =  "00:1A:2B:3C:4D:5E";
    private final String parentMacAddress = "60:7A:8B:9C:4D:5E";

    @Test
    void whenMapDeviceDtoToDeviceEntity_thenReturnDeviceEntity() {
        var registrationDeviceRequest = Instancio.of(RegisterDeviceRequest.class)
                .set(field(RegisterDeviceRequest::getDeviceType), DeviceType.GATEWAY)
                .set(field(RegisterDeviceRequest::getMacAddress), parentMacAddress)
                .create();

        var resultDeviceEntity = deviceMapper.mapDeviceDtoToDeviceEntity(registrationDeviceRequest);
        assertNotNull(resultDeviceEntity);
        assertEquals(DeviceType.GATEWAY, resultDeviceEntity.getDeviceType());
        assertEquals(parentMacAddress, resultDeviceEntity.getMacAddress());
    }

    @Test
    void whenMapDeviceDtoToDeviceEntity_thenReturnEmptyDeviceEntity() {
        var resultDeviceEntity = deviceMapper.mapDeviceDtoToDeviceEntity(null);
        assertNotNull(resultDeviceEntity);
        assertNull(resultDeviceEntity.getDeviceType());
        assertNull(resultDeviceEntity.getMacAddress());
    }

    @Test
    void whenMapDeviceEntityToRegisterDeviceResponse_thenReturnRegisterDeviceResponse() {
        final UUID id = UUID.randomUUID();
        var childDeviceEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getDeviceType), DeviceType.SWITCH)
                .set(field(DeviceEntity::getMacAddress), childMacAddress)
                .set(field(DeviceEntity::getUplinkDevice), Instancio.of(DeviceEntity.class)
                        .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                        .create())
                .set(field(DeviceEntity::getId), id)
                .create();

        var resultRegisterDeviceResponse = deviceMapper.mapDeviceEntityToRegisterDeviceResponse(childDeviceEntity);

        assertNotNull(resultRegisterDeviceResponse);
        assertEquals(DeviceType.SWITCH, resultRegisterDeviceResponse.getDeviceType());
        assertEquals(childMacAddress, resultRegisterDeviceResponse.getMacAddress());
        assertEquals(parentMacAddress, resultRegisterDeviceResponse.getUplinkMacAddress());
        assertEquals(id, resultRegisterDeviceResponse.getId());
    }

    @Test
    void whenMapDeviceEntityToRegisterDeviceResponseDeviceResponse_thenReturnEmptyRegisterDeviceResponse() {
        var resultRegisterDeviceResponse = deviceMapper.mapDeviceEntityToRegisterDeviceResponse(null);

        assertNotNull(resultRegisterDeviceResponse);
        assertNull(resultRegisterDeviceResponse.getDeviceType());
        assertNull(resultRegisterDeviceResponse.getMacAddress());
        assertNull(resultRegisterDeviceResponse.getUplinkMacAddress());
        assertNull(resultRegisterDeviceResponse.getId());
    }

    @Test
    void whenMapDeviceEntityToDeviceResponse_thenReturnDeviceResponse() {
        final UUID id = UUID.randomUUID();
        var childDeviceEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getDeviceType), DeviceType.SWITCH)
                .set(field(DeviceEntity::getMacAddress), childMacAddress)
                .set(field(DeviceEntity::getUplinkDevice), Instancio.of(DeviceEntity.class)
                        .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                        .create())
                .set(field(DeviceEntity::getId), id)
                .create();

        var resultRegisterDeviceResponse = deviceMapper.mapDeviceEntityToDeviceResponse(childDeviceEntity);

        assertNotNull(resultRegisterDeviceResponse);
        assertEquals(DeviceType.SWITCH, resultRegisterDeviceResponse.getDeviceType());
        assertEquals(childMacAddress, resultRegisterDeviceResponse.getMacAddress());
    }

    @Test
    void whenMapDeviceEntityToDeviceResponseDeviceResponse_thenReturnEmptyDeviceResponse() {
        var resultRegisterDeviceResponse = deviceMapper.mapDeviceEntityToDeviceResponse(null);

        assertNotNull(resultRegisterDeviceResponse);
        assertNull(resultRegisterDeviceResponse.getDeviceType());
        assertNull(resultRegisterDeviceResponse.getMacAddress());
    }

    @Test
    void whenMapEntityToTopologyNodeResponse_thenReturnTopologyNodeResponse() {
        var parentDeviceEntity = Instancio.of(DeviceEntity.class)
                .set(field(DeviceEntity::getMacAddress), parentMacAddress)
                .create();

        var resultTopologyNodeResponse = deviceMapper.mapEntityToTopologyNodeResponse(parentDeviceEntity);

        assertNotNull(resultTopologyNodeResponse);
        assertEquals(parentMacAddress, resultTopologyNodeResponse.getMacAddress());
        assertEquals(0, resultTopologyNodeResponse.getChildren().size());
    }

    @Test
    void whenMapEntityToTopologyNodeResponse_thenReturnEmptyTopologyNodeResponse() {
        var resultTopologyNodeResponse = deviceMapper.mapEntityToTopologyNodeResponse(null);

        assertNotNull(resultTopologyNodeResponse);
        assertNull(resultTopologyNodeResponse.getMacAddress());
    }
}