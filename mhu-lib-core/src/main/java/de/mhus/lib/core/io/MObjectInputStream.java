package de.mhus.lib.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class MObjectInputStream extends ObjectInputStream {

	private ClassLoader cl = getClass().getClassLoader();

	public MObjectInputStream() throws IOException, SecurityException {
		super();
	}

	public MObjectInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	public void setClassLoader(ClassLoader cl) {
		this.cl = cl;
	}

    @Override
	protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException
    {
        String name = desc.getName();
        try {
            return Class.forName(name, false, cl );
        } catch (ClassNotFoundException ex) {
        	return super.resolveClass(desc);
        }
    }

    protected Class<?> resolveProxyClass(String[] interfaces)
            throws IOException, ClassNotFoundException
    {
        ClassLoader latestLoader = cl;
        ClassLoader nonPublicLoader = null;
        boolean hasNonPublicInterface = false;

        // define proxy in class loader of non-public interface(s), if any
        Class[] classObjs = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            Class cl = Class.forName(interfaces[i], false, latestLoader);
            if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
                if (hasNonPublicInterface) {
                    if (nonPublicLoader != cl.getClassLoader()) {
                        throw new IllegalAccessError(
                            "conflicting non-public interface class loaders");
                    }
                } else {
                    nonPublicLoader = cl.getClassLoader();
                    hasNonPublicInterface = true;
                }
            }
            classObjs[i] = cl;
        }
        try {
            return Proxy.getProxyClass(
                hasNonPublicInterface ? nonPublicLoader : latestLoader,
                classObjs);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }
    
}