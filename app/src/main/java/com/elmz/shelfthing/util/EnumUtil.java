package com.elmz.shelfthing.util;

import android.content.Intent;
import android.os.Bundle;

public final class EnumUtil {
	public static class Serializer<T extends Enum<T>> {
		private final T victim;
		private final String name;

		public Serializer(T victim) {
			this.name = victim.getClass().getName();
			this.victim = victim;
		}

		public void to(Intent intent) {
			intent.putExtra(name, victim.ordinal());
		}

		public void to(Bundle bundle) {
			bundle.putInt(name, victim.ordinal());
		}
	}

	public static class Deserializer<T extends Enum<T>> {
		private final Class<T> victimType;
		private final String name;

		public Deserializer(Class<T> victimType) {
			this.victimType = victimType;
			this.name = victimType.getName();
		}

		public T from(Intent intent) {
			if (!intent.hasExtra(name)) throw new IllegalStateException();
			return victimType.getEnumConstants()[intent.getIntExtra(name, -1)];
		}

		public T from(Bundle bundle) {
			if (!bundle.containsKey(name)) throw new IllegalStateException();
			return victimType.getEnumConstants()[bundle.getInt(name, -1)];
		}
	}

	public static <T extends Enum<T>> Deserializer<T> deserialize(Class<T> victim) {
		return new Deserializer<>(victim);
	}

	public static <T extends Enum<T>> Serializer<T> serialize(T victim) {
		return new Serializer<>(victim);
	}
}
