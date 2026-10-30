package mx.abraham.detonamelo.logic;

/**
 * Bandera anti-recursion: al romper bloques extra desde Detonate/Treecapitator
 * usamos gameMode.destroyBlock, que vuelve a disparar PlayerBlockBreakEvents.AFTER.
 * Esta bandera evita que esos bloques extra detonen su propia area.
 */
public final class BreakGuard {
	private static final ThreadLocal<Boolean> BREAKING_EXTRA = ThreadLocal.withInitial(() -> Boolean.FALSE);

	private BreakGuard() {
	}

	public static boolean isBreakingExtra() {
		return BREAKING_EXTRA.get();
	}

	public static void runGuarded(Runnable action) {
		BREAKING_EXTRA.set(Boolean.TRUE);
		try {
			action.run();
		} finally {
			BREAKING_EXTRA.set(Boolean.FALSE);
		}
	}
}
