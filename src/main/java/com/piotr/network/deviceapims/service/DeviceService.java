package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;

import java.util.List;
import java.util.Optional;


public interface DeviceService {

    List<TopologyNodeResponse> getTopologyNodes();
    Optional<TopologyNodeResponse> getTopologyNode(String macAddress);
    List<RegisterDeviceResponse>  getDevices();
    Optional<RegisterDeviceResponse>  getDeviceByMac(String macAddress);
    RegisterDeviceResponse registerDevice(RegisterDeviceRequest registerDeviceRequest);
}
