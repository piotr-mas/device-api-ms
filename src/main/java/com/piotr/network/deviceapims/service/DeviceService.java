package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.generated.model.DeviceResponse;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;

import java.util.List;


public interface DeviceService {

    List<TopologyNodeResponse> getTopologyNodes();
    TopologyNodeResponse getTopologyNodeByMac(String macAddress);
    List<DeviceResponse>  getDevices();
    RegisterDeviceResponse registerDevice(RegisterDeviceRequest registerDeviceRequest);
    DeviceResponse  getDeviceByMac(String macAddress);
}
