package gytis.courier.application.command;

public record AddItemNoteCommand(
        Long myId,
        Long taskId,
        Long itemId,
        String note
) {
}
