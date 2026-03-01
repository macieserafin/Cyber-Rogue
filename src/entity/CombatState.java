package entity;

public enum CombatState {
    MELEE, RANGE;

    public CombatState next() {
        CombatState[] values = CombatState.values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
