package com.csware.divi_dead.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {

	public interface PoolObjectFactory<T> {
		public T createObject();
	}

	private final List<T>				m_FreeObjects;
	private final PoolObjectFactory<T>	m_Factory;
	private final int					m_MaxSize;

	public Pool(PoolObjectFactory<T> p_Factory, int p_MaxSize) {
		this.m_Factory = p_Factory;
		this.m_MaxSize = p_MaxSize;
		this.m_FreeObjects = new ArrayList<T>(p_MaxSize);
	}

	public T newObject() {
		T object = null;

		if (m_FreeObjects.isEmpty()) object = m_Factory.createObject();
		else object = m_FreeObjects.remove(m_FreeObjects.size() - 1);
		return object;
	}

	public void free(T p_Object) {
		if (m_FreeObjects.size() < m_MaxSize) m_FreeObjects.add(p_Object);
	}
}
