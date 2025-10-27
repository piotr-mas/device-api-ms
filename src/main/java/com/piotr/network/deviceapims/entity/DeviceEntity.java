package com.piotr.network.deviceapims.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.piotr.network.deviceapims.generated.model.DeviceType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@JsonPropertyOrder({"id", "deviceType", "macAddress"})
public class DeviceEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(unique = true)
    private String macAddress;

    //Parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uplinkId")
    private DeviceEntity uplinkDevice;
    //Children
    @OneToMany(mappedBy = "uplinkDevice", fetch = FetchType.LAZY)
    private List<DeviceEntity> downlinkDevices;

}
