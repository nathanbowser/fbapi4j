package com.fieldexpert.fbapi4j;

public class Area extends Entity {

	Area(Integer id, String name, Integer owner, Integer project) {
		fields.put(Fbapi4j.IX_AREA, id);
		fields.put(Fbapi4j.S_AREA, name);
		fields.put(Fbapi4j.IX_PERSON_OWNER, owner);
		fields.put(Fbapi4j.IX_PROJECT, project);
	}

	public Area(String name, Integer owner, Integer project) {
		fields.put(Fbapi4j.S_AREA, name);
		fields.put(Fbapi4j.IX_PERSON_OWNER, owner);
		fields.put(Fbapi4j.IX_PROJECT, project);
	}

	public Area(String name, Person owner, Project project) {
		this(name, owner.getId(), project.getId());
	}

	public Area(String name, Person owner, Integer project) {
		this(name, owner.getId(), project);
	}

	public Area(String name, Integer owner, Project project) {
		this(name, owner, project.getId());
	}

	public Integer getId() {
		return (Integer) fields.get(Fbapi4j.IX_AREA);
	}

	public String getName() {
		return (String) fields.get(Fbapi4j.S_AREA);
	}

	public Person getOwner() {
		Session session = SessionFactory.getCurrentSession();
		return session.get(Person.class, (Integer) fields.get(Fbapi4j.IX_PERSON_OWNER));
	}

	public Project getProject() {
		Session session = SessionFactory.getCurrentSession();
		return session.get(Project.class, (Integer) fields.get(Fbapi4j.IX_PROJECT));
	}

}
