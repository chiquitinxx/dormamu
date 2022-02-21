package dev.yila.dormamu;

public class Change {

    private final String tableName;
    private final ChangeType type;
    private final Row before;
    private final Row after;

    public static Change insert(String tableName, Row newRow) {
        return new Change(tableName, ChangeType.NEW_ROW, null, newRow);
    }

    public static Change delete(String tableName, Row deletedRow) {
        return new Change(tableName, ChangeType.DELETED_ROW, deletedRow, null);
    }

    public static Change update(String tableName, Row before, Row after) {
        return new Change(tableName, ChangeType.UPDATED_ROW, before, after);
    }

    private Change(String tableName, ChangeType type, Row before, Row after) {
        this.tableName = tableName;
        this.type = type;
        this.before = before;
        this.after = after;
    }

    public String getTableName() {
        return tableName;
    }

    public ChangeType getType() {
        return type;
    }

    public Row getBefore() {
        return before;
    }

    public Row getAfter() {
        return after;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Change
                && this.tableName.equals(((Change) obj).tableName)
                && this.type.equals(((Change) obj).type)
                && this.after.equals(((Change) obj).after)
                && this.before.equals(((Change) obj).before);
    }

    @Override
    public String toString() {
        return this.type.name() + " in table: " + this.tableName + " before: " + before + " after: " + after;
    }
}
