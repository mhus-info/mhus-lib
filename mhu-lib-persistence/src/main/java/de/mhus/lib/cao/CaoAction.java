package de.mhus.lib.cao;

import de.mhus.lib.core.directory.MResourceProvider;
import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;
import de.mhus.lib.form.MForm;

/**
 * A action is doing something with one or more objects. The main
 * difference to a operation is that the objects do not have
 * meanings. A action can call a operation to execute.
 * 
 * @author mikehummel
 *
 */
public abstract class CaoAction extends MObject {

	private MNls resourceBundle;

	public CaoAction() {
		resourceBundle = new MNls();
	}

	public CaoAction(MResourceProvider<?> res, String resourceName) {
		resourceBundle = base(MNlsFactory.class).load(res,this.getClass(), resourceName, null);
	}

	public abstract String getName();

	/**
	 * Returns a configuration Form for the operation. The list of elements
	 * should be a representative list. The configuration use most time the
	 * first element of this list. In other cases the hole list is needed,
	 * it depends on the operation.
	 * 
	 * @param list
	 * @param initConfig specific initial attributes
	 * @return
	 * @throws CaoException 
	 */
	public abstract MForm createConfiguration(CaoList list,Object...initConfig) throws CaoException;

	public abstract boolean canExecute(CaoList list, Object...initConfig);

	/**
	 * Executes a defined action. Is the action need to execute an operation it will
	 * return the operation object to be executed by the caller.
	 * 
	 * @param list
	 * @param configuration
	 * @return
	 * @throws CaoException
	 */
	public abstract CaoOperation execute(CaoList list, Object configuration) throws CaoException;

	@Override
	public String toString() {
		return "Action " + getName() + " (" + getClass().getCanonicalName() + ")";
	}

	public MNls getResourceBundle() {
		return resourceBundle;
	}

}
