package com.piotr.network.deviceapims.repository;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {
    Optional<DeviceEntity> findByMacAddress(String macAddress);
    List<DeviceEntity> findByUplinkDeviceIsNull();
}
