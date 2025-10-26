package com.piotr.network.deviceapims.controller;

import com.piotr.network.deviceapims.generated.api.DevicesApi;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import com.piotr.network.deviceapims.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class DeviceController implements DevicesApi {

    private final DeviceService deviceService;
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public ResponseEntity<List<TopologyNodeResponse>> devicesTopologyGet() {
        var result = deviceService.getTopologyNodes();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<TopologyNodeResponse> devicesTopologyMacAddressGet(String macAddress) {
        Optional<TopologyNodeResponse> result = deviceService.getTopologyNode(macAddress);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<RegisterDeviceResponse>> getAllDevices() {
        var  result = deviceService.getDevices();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<RegisterDeviceResponse> getDeviceByMac(String macAddress) {
        Optional<RegisterDeviceResponse> result = deviceService.getDeviceByMac(macAddress);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<RegisterDeviceResponse> registerDevice(RegisterDeviceRequest registerDeviceRequest) {
        var result = deviceService.registerDevice(registerDeviceRequest);
        return ResponseEntity.status(201).body(result);
    }
}
