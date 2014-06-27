package com.exo.engine;


public interface Manager<T> {

	void process(T instance) throws Exception;

	void send(T instance) throws Exception;

	void save(T instance)throws Exception;

}
