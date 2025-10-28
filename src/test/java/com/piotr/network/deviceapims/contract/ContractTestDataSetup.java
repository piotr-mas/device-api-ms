package com.piotr.network.deviceapims.contract;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.generated.model.DeviceType;
import com.piotr.network.deviceapims.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class ContractTestDataSetup {

    @Autowired
    private DeviceRepository deviceRepository;

    @PostConstruct
    public void init() {
        var gateway = new DeviceEntity();
        gateway.setMacAddress("70:1A:2B:3C:4D:5E");
        gateway.setDeviceType(DeviceType.GATEWAY);
        deviceRepository.save(gateway);

        var accessPoint = new DeviceEntity();
        accessPoint.setMacAddress("AA:BB:CC:DD:EE:FF");
        accessPoint.setDeviceType(DeviceType.ACCESS_POINT);
        accessPoint.setUplinkDevice(gateway);
        deviceRepository.save(accessPoint);
    }
}
