package com.piotr.network.deviceapims.mapper;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DeviceMapper {

    public DeviceEntity mapDeviceDtoToDeviceEntity(final RegisterDeviceRequest dto) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setDeviceType(dto.getDeviceType());
        deviceEntity.setMacAddress(dto.getMacAddress());
        return deviceEntity;
    }

    public RegisterDeviceResponse  mapDeviceEntityToDeviceResponse(final DeviceEntity entity) {
        RegisterDeviceResponse  deviceResponse = new RegisterDeviceResponse();
        var uuid =entity.getId();
        deviceResponse.setId(uuid);
        deviceResponse.setDeviceType(entity.getDeviceType());
        deviceResponse.setMacAddress(entity.getMacAddress());
        if (entity.getUplinkDevice()!=null) {
            deviceResponse.setUplinkMacAddress(entity.getUplinkDevice().getMacAddress());
        }
        return deviceResponse;
    }

    public TopologyNodeResponse mapEntityToTopologyNodeResponse(final DeviceEntity entity) {
        TopologyNodeResponse node = new TopologyNodeResponse();
        node.setMacAddress(entity.getMacAddress());
        node.setChildren(new ArrayList<>());
        return node;
    }
}
