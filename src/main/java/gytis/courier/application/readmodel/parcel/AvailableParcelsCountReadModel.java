package gytis.courier.application.readmodel.parcel;

public record AvailableParcelsCountReadModel(
        Long pickingUpCount,
        Long deliveringCount
/*        Long failedCount*/
) {
}
