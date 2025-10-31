package com.piotr.network.deviceapims.mapper;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.generated.model.DeviceResponse;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DeviceMapper {

    /**
     * mapDeviceDtoToDeviceEntity
     * @param dto object to map
     * @return DeviceEntity
     */
    public DeviceEntity mapDeviceDtoToDeviceEntity(final RegisterDeviceRequest dto) {
        DeviceEntity deviceEntity = new DeviceEntity();
        if (dto == null) {
            return deviceEntity;
        }
        deviceEntity.setDeviceType(dto.getDeviceType());
        deviceEntity.setMacAddress(dto.getMacAddress());
        return deviceEntity;
    }

    /**
     * mapDeviceEntityToRegisterDeviceResponse
     * @param entity object to map
     * @return RegisterDeviceResponse
     */
    public RegisterDeviceResponse  mapDeviceEntityToRegisterDeviceResponse(final DeviceEntity entity) {
        RegisterDeviceResponse  deviceResponse = new RegisterDeviceResponse();
        if (entity == null) {
            return deviceResponse;
        }
        var uuid =entity.getId();
        deviceResponse.setId(uuid);
        deviceResponse.setDeviceType(entity.getDeviceType());
        deviceResponse.setMacAddress(entity.getMacAddress());
        if (entity.getUplinkDevice()!=null) {
            deviceResponse.setUplinkMacAddress(entity.getUplinkDevice().getMacAddress());
        }
        return deviceResponse;
    }

    /**
     * mapDeviceEntityToDeviceResponse
     * @param entity object to map
     * @return DeviceResponse
     */
    public DeviceResponse mapDeviceEntityToDeviceResponse(final DeviceEntity entity) {
        DeviceResponse  deviceResponse = new DeviceResponse();
        if (entity == null) {
            return deviceResponse;
        }
        deviceResponse.setDeviceType(entity.getDeviceType());
        deviceResponse.setMacAddress(entity.getMacAddress());
        return deviceResponse;
    }

    /**
     * mapEntityToTopologyNodeResponse
     * @param entity object to map
     * @return TopologyNodeResponse
     */
    public TopologyNodeResponse mapEntityToTopologyNodeResponse(final DeviceEntity entity) {
        TopologyNodeResponse node = new TopologyNodeResponse();
        if (entity == null) {
            return node;
        }
        node.setMacAddress(entity.getMacAddress());
        node.setChildren(new ArrayList<>());
        return node;
    }
}
