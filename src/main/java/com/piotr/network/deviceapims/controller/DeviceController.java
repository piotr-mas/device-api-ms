package com.piotr.network.deviceapims.controller;

import com.piotr.network.deviceapims.generated.api.DevicesApi;
import com.piotr.network.deviceapims.generated.model.DeviceResponse;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import com.piotr.network.deviceapims.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class DeviceController implements DevicesApi {

    private final DeviceService deviceService;
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Get device topology
     * @return List of TopologyNodeResponse
     */
    @Override
    public ResponseEntity<List<TopologyNodeResponse>> devicesTopologyGet() {
        var result = deviceService.getTopologyNodes();
        return ResponseEntity.ok(result);
    }

    /**
     * Get devices Topology by Mac Address
     * @param macAddress the data transfer object containing user input for processing
     * @return TopologyNodeResponse
     */
    @Override
    public ResponseEntity<TopologyNodeResponse> devicesTopologyMacAddressGet(String macAddress) {
        var result = deviceService.getTopologyNodeByMac(macAddress);
        return ResponseEntity.ok(result);
    }

    /**
     * Get All Devices
     * @return List of DeviceResponse
     */
    @Override
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        var  result = deviceService.getDevices();
        return ResponseEntity.ok(result);
    }

    /**
     * Get Device By Mac
     * @param macAddress the data transfer object containing user input for processing
     * @return DeviceResponse
     */
    @Override
    public ResponseEntity<DeviceResponse> getDeviceByMac(String macAddress) {
        var result = deviceService.getDeviceByMac(macAddress);
        return ResponseEntity.ok(result);
    }

    /**
     * Register Device
     * @param registerDeviceRequest the data transfer object containing user input for processing
     * @return RegisterDeviceResponse
     */
    @Override
    public ResponseEntity<RegisterDeviceResponse> registerDevice(RegisterDeviceRequest registerDeviceRequest) {
        var result = deviceService.registerDevice(registerDeviceRequest);
        return ResponseEntity.status(201).body(result);
    }
}
