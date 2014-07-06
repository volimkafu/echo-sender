package com.exo.engine;

public interface Manager<T> {

	void send(T instance) throws Exception;
}
