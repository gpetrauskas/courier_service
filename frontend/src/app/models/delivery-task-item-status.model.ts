export enum DeliveryTaskItemStatus {
  PICKING_UP = 'PICKING_UP',
  PICKED_UP = 'PICKED_UP',
  DELIVERING = 'DELIVERING',
  DELIVERED = 'DELIVERED',
  FAILED_PICKUP = 'FAILED_PICKUP',
  FAILED_DELIVERY = 'FAILED_DELIVERY',
  CANCELED = 'CANCELED'
}

export const StatusesByTaskType: { [ key: string ]: DeliveryTaskItemStatus[] } = {
  PICKUP: [
    DeliveryTaskItemStatus.PICKING_UP,
    DeliveryTaskItemStatus.PICKED_UP,
    DeliveryTaskItemStatus.FAILED_PICKUP,
    DeliveryTaskItemStatus.CANCELED
  ],
  DELIVERY: [
    DeliveryTaskItemStatus.DELIVERING,
    DeliveryTaskItemStatus.DELIVERED,
    DeliveryTaskItemStatus.FAILED_DELIVERY,
    DeliveryTaskItemStatus.CANCELED
  ]
}
