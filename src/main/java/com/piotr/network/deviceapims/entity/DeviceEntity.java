package com.piotr.network.deviceapims.entity;

import com.piotr.network.deviceapims.generated.model.DeviceType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeviceEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String macAddress;

    //Parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uplinkId")
    private DeviceEntity uplinkDevice;
    //Children
    @OneToMany(mappedBy = "uplinkDevice", fetch = FetchType.LAZY)
    private List<DeviceEntity> downlinkDevices;

}
