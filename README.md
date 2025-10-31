# device-api-ms
Device API Network deployment 
# Description
## Network deployment might consist of several devices.
### Networking device might be of following types:
- Gateway - serves as access point to another network
- Switch - connects devices on a computer network
- Access Point - connects devices on a computer network via Wi-Fi

Typically, these devices are connected to one another and collectively form a
network deployment.

Every device on a computer network can be identified by MAC address.

If device is attached to another device in same network, it is represented via
uplink reference.

# Task
### Define and implement Device API, which should support following features:
- Registering a device to a network deployment
  - input: deviceType, macAddress, uplinkMacAddress
- Retrieving all registered devices, sorted by device type
  - output: sorted list of devices, where each entry has deviceType and macAddress (sorting order: Gateway > Switch > Access Point)
- Retrieving network deployment device by MAC address
  - input: macAddress
  - output: Device entry, which consists of deviceType and macAddress
- Retrieving all registered network device topology
  - output: Device topology as tree structure, node should be represented as macAddress
- Retrieving network device topology starting from a specific device
  - input: macAddress
  - output: Device topology where root node is device with matching macAddress
###  Additional notes:
Device may or may not be connected to uplink device