package com.piotr.network.deviceapims.service;

import com.piotr.network.deviceapims.entity.DeviceEntity;
import com.piotr.network.deviceapims.exception.InvalidRequestException;
import com.piotr.network.deviceapims.generated.model.DeviceType;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceRequest;
import com.piotr.network.deviceapims.generated.model.RegisterDeviceResponse;
import com.piotr.network.deviceapims.generated.model.TopologyNodeResponse;
import com.piotr.network.deviceapims.mapper.DeviceMapper;
import com.piotr.network.deviceapims.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final DeviceMapper mapper;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository, DeviceMapper mapper) {
        this.deviceRepository = deviceRepository;
        this.mapper = mapper;
    }

    private static final Map<DeviceType, Integer> DEVICE_ORDER = Map.of(
            DeviceType.GATEWAY, 1,
            DeviceType.SWITCH, 2,
            DeviceType.ACCESS_POINT, 3
    );

    private static final String NOT_FOUND = " not found";

    /**
     * Registering a device to a network deployment
     * @param dto
     * @return
     */
    @Override
    public RegisterDeviceResponse registerDevice(RegisterDeviceRequest dto) {
        var entity = mapper.mapDeviceDtoToDeviceEntity(dto);
        var parentMac = dto.getUplinkMacAddress();
        if (parentMac != null && !parentMac.isBlank()) {
            var parent = deviceRepository.findByMacAddress(parentMac)
                    .orElseThrow(() -> new InvalidRequestException(HttpStatus.BAD_REQUEST, "Parent device with MAC "+ parentMac + NOT_FOUND));
            entity.setUplinkDevice(parent);
        }
        var entityResult = deviceRepository.save(entity);
        return mapper.mapDeviceEntityToDeviceResponse(entityResult);
    }

    /**
     * Retrieving all registered devices, sorted by device type
     * @return
     */
    @Override
    public List<RegisterDeviceResponse> getDevices() {
        var allDevices = deviceRepository.findAll();
        if  (allDevices.isEmpty()) {
            throw new InvalidRequestException(HttpStatus.NOT_FOUND, "No device(s) found");
        }
        return allDevices.stream()
                .sorted(Comparator.comparing(
                        DeviceEntity::getDeviceType,
                        Comparator.comparing(DEVICE_ORDER::get)
                ))
                .map(mapper::mapDeviceEntityToDeviceResponse)
                .toList();
    }

    /**
     * Retrieving network deployment device by MAC address
     * @param macAddress
     * @return
     */
    @Override
    public RegisterDeviceResponse getDeviceByMac(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress)
                .map(mapper::mapDeviceEntityToDeviceResponse)
                .orElseThrow(() -> new InvalidRequestException(HttpStatus.NOT_FOUND, "Device with MAC "+ macAddress + NOT_FOUND));

    }

    /**
     * Retrieving all registered network device topology as tree structure
     * @return
     */
    @Override
    public List<TopologyNodeResponse> getTopologyNodes() {
        var rootNodeEntities = deviceRepository.findByUplinkDeviceIsNull();
        if  (rootNodeEntities.isEmpty()) {
            throw new InvalidRequestException(HttpStatus.NOT_FOUND, "No device(s) found in network topology");
        }
        return rootNodeEntities.stream()
                .map(this::buildTopologyTreeResponse)
                .toList();
    }

    @Override
    public TopologyNodeResponse getTopologyNode(String macAddress) {
        var parent = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new InvalidRequestException(HttpStatus.NOT_FOUND, "Topology with device MAC "+ macAddress + NOT_FOUND));
        return buildTopologyTreeResponse(parent);
    }

    /**
     * Recursively builds the TopologyNodeResponse tree structure.
     * @param parentEntity
     * @return
     */
    private TopologyNodeResponse buildTopologyTreeResponse(DeviceEntity parentEntity) {
        TopologyNodeResponse topologyNodeResponse = mapper.mapEntityToTopologyNodeResponse(parentEntity);
        var children = parentEntity.getDownlinkDevices();
        if (children != null && !children.isEmpty()) {
            var childNodes = children.stream()
                    .map(this::buildTopologyTreeResponse)
                    .toList();
            topologyNodeResponse.setChildren(childNodes);
        }
        return topologyNodeResponse;
    }
}
