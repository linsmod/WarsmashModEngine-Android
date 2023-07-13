package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CWeaponType {
	NONE,

	NORMAL,
	INSTANT,
	ARTILLERY,
	ALINE,
	MISSILE,
	MSPLASH,
	MBOUNCE,
	MLINE;

	public static CWeaponType parseWeaponType(final String weaponTypeString) {
		if(weaponTypeString=="_")
			return CWeaponType.NONE;
		return valueOf(weaponTypeString.toUpperCase());
	}

	public boolean isAttackGroundSupported() {
		return (this == CWeaponType.ARTILLERY) || (this == CWeaponType.ALINE);
	}
}
