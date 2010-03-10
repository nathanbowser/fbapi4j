package com.fieldexpert.fbapi4j;

import java.util.List;

public interface Session {

	void close();

	void close(Case bug);

	void create(Entity t);

	<T extends Entity> T get(Class<T> clazz, Long id);

	<T extends Entity> List<T> findAll(Class<T> clazz);

	void edit(Case bug);

	void reactivate(Case bug);

	void reopen(Case bug);

	void resolve(Case bug);

	void scout(Case bug);

}
