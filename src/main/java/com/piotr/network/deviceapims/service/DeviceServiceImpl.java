package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Override
    public List<TopologyNodeResponse> getTopologyNodes() {
        return List.of();
    }

    @Override
    public Optional<TopologyNodeResponse> getTopologyNode(String macAddress) {
        return Optional.empty();
    }

    @Override
    public List<RegisterDeviceResponse> getDevices() {
        return List.of();
    }

    @Override
    public Optional<RegisterDeviceResponse> getDeviceByMac(String macAddress) {
        return Optional.empty();
    }

    @Override
    public RegisterDeviceResponse registerDevice(RegisterDeviceRequest registerDeviceRequest) {
        return null;
    }
}
