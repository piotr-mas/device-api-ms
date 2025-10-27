package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;

import java.util.List;


public interface DeviceService {

    List<TopologyNodeResponse> getTopologyNodes();
    TopologyNodeResponse getTopologyNode(String macAddress);
    List<RegisterDeviceResponse>  getDevices();
    RegisterDeviceResponse registerDevice(RegisterDeviceRequest registerDeviceRequest);
    RegisterDeviceResponse  getDeviceByMac(String macAddress);
}
