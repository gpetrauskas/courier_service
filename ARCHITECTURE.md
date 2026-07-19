# Architecture Decisions

## ParcelStatus: used by TaskItem and Parcel
Parcel lifecycle (system-driven):
WAITING_FOR_PAYMENT -> PICKING_UP -> PICKED_UP -> DELIVERING -> DELIVERED

TaskItem workflow(courier-driven):
PICKING_UP -> PICKED_UP | FAILED_PICKUP
DELIVERING -> DELIVERED | FAILED_DELIVERY

Both use ParcelStatus enum. TaskItem is a snapshot, not the real Parcel.