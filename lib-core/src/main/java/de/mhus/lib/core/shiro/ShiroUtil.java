package de.mhus.lib.core.shiro;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.logging.Log;

public class ShiroUtil {

    private static final Log log = Log.getLog(ShiroUtil.class);
    public static final String ROLE_ADMIN = "GLOBAL_ADMIN";
    private static final Object ATTR_LOCALE = "locale";

    public static boolean isAdmin() {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        return subject.hasRole(ROLE_ADMIN);
    }
    
    public static Subject getSubject() {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        return subject;
    }
    
    public static boolean isAuthenticated() {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        return subject.isAuthenticated();
    }
    
    public static String getPrincipal() {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        return getPrincipal(subject);
    }
    
    public static String getPrincipal(Subject subject) {
        Object p = subject.getPrincipal();
        if (p == null) return null;
        return String.valueOf(p);
    }

    public static String toString(Subject subject) {
        if (subject == null) return "null";
        if (!subject.isAuthenticated()) return "[guest]";
        Object p = subject.getPrincipal();
        if (p == null) return "[?]";
        return String.valueOf(p);
    }

    public static void subjectCleanup() {
        ThreadContext.remove();
    }

    public static SubjectEnvironment useSubject(Subject subject) {
        Subject current = ThreadContext.getSubject();
        ThreadContext.bind(subject);
        return new SubjectEnvironment(subject, current);
    }
    
    public static Collection<Realm> getRealms() {
        SecurityManager securityManager = M.l(ShiroSecurity.class).getSecurityManager();
        return ((RealmSecurityManager)securityManager).getRealms();
    }
    
    public static PrincipalData loadPrincipalDataFromRealm(Subject subject) {
        if (!subject.isAuthenticated()) return null;
        for (Realm realm : getRealms()) {
            if (realm instanceof PrincipalDataRealm) {
                Map<String,String> data = ((PrincipalDataRealm)realm).getUserData(subject);
                if (data != null) {
                    data.put(PrincipalData.NAME, String.valueOf(subject.getPrincipal()));
                    if (!data.containsKey(PrincipalData.DISPLAY_NAME))
                        data.put(PrincipalData.DISPLAY_NAME, String.valueOf(subject.getPrincipal()));
                    return new PrincipalData(data);
                }
            }
        }
        return null;
    }

    public static void loadPrincipalData(Subject subject) {
        synchronized (subject) {
            if (subject.getSession().getAttribute(PrincipalData.SESSION_KEY) == null) {
                PrincipalData data = loadPrincipalDataFromRealm(subject);
                if (data == null) {
                    Map<String,String> map = new HashMap<>();
                    map.put(PrincipalData.NAME, String.valueOf(subject.getPrincipal()));
                    map.put(PrincipalData.DISPLAY_NAME, String.valueOf(subject.getPrincipal()));
                    data = new PrincipalData(map);
                }
                subject.getSession().setAttribute(PrincipalData.SESSION_KEY, data);
            }
        }
    }

    public static PrincipalData getPrincipalData(Subject subject) {
        loadPrincipalData(subject);
        return (PrincipalData) subject.getSession().getAttribute(PrincipalData.SESSION_KEY);
    }

    public static PrincipalData getPrincipalData() {
        return getPrincipalData(getSubject());
    }
    
    public static Subject createSubjectFromSessionId(String sessionId) {
        return new Subject.Builder().sessionId(sessionId).buildSubject();
    }

    public static String getSessionId(boolean create) {
        Session session = getSubject().getSession(create);
        return session == null ? null : String.valueOf(session.getId());
    }

    public static boolean isPermitted(List<String> rules, Class<?> permission, String level,Object instance) {
        return isPermitted(rules, permission == null ? null : permission.getCanonicalName(), level, instance == null ? null : instance.toString());
    }
    
    /**
     * Syntax:
     * 
     * # - comment
     * authenticated
     * !authenticated
     * user:
     * !user:
     * role:
     * !role
     * permission:
     * !permission:
     * 
     * In permission the replacements:
     * ${permission}
     * ${level}
     * ${instance}
     * 
     * @param rules
     * @param permission
     * @param level
     * @param instance
     * @return true if access is granted
     */
    public static boolean isPermitted(List<String> rules, String permission, String level,String instance) {
        // check rules
        Subject subject = getSubject();
        String principal = getPrincipal(subject);
        for (String rule : rules) {
            rule = rule.trim();
            if (rule.isEmpty() || rule.startsWith("#")) continue;
            if (rule.equals("authenticated")) {
                if (!subject.isAuthenticated()) return false;
            } else if (rule.equals("!authenticated")) {
                if (subject.isAuthenticated()) return false;
            } else if (rule.startsWith("user:")) {
                if (!rule.substring(5).equals(principal)) return false;
            } else if (rule.startsWith("!user:")) {
                if (rule.substring(6).equals(principal)) return false;
            } else if (rule.startsWith("role:")) {
                if (!subject.hasRole(rule.substring(5))) return false;
            } else if (rule.startsWith("!role:")) {
                if (subject.hasRole(rule.substring(6))) return false;
            } else if (rule.startsWith("permission:")) {
                String perm = rule.substring(11);
                perm = replacePermission(perm, permission, level, instance);
                if (!subject.isPermitted(new WildcardPermission(perm))) return false;
            } else if (rule.startsWith("!permission:")) {
                String perm = rule.substring(12);
                perm = replacePermission(perm, permission, level, instance);
                if (subject.isPermitted(new WildcardPermission(perm))) return false;
            }

        }
        return true;
    }
    
    private static String replacePermission(String perm, String permission, String level, String instance) {
        if (!perm.contains("${")) return perm;

        permission = normalizeWildcardPart(permission);
        level = normalizeWildcardPart(level);
        instance = normalizeWildcardPart(instance);

        perm = perm.replaceAll("\\${permission}", permission);
        perm = perm.replaceAll("\\${level}", level);
        perm = perm.replaceAll("\\${instance}", instance);

        return perm;
    }

    /**
     * https://shiro.apache.org/permissions.html
     * 
     * @param permission Insert a permission, what kind should be checked, e.g. printer, file - or null
     * @param level Insert a action or level what should be done with the kind - or null
     * @param instance The name of the exact instance, e.g. id or path - or null
     * @return true if access is granted
     */
    public static boolean isPermitted(String permission, String level, String instance) {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        permission = normalizeWildcardPart(permission);
        StringBuilder wildcardString = new StringBuilder().append(permission);
        if (level != null || instance != null) {
            if (level == null)
                wildcardString.append(":*");
            else {
                level = normalizeWildcardPart(level);
                wildcardString.append(':').append(level);
            }
            if (instance != null) {
                instance = normalizeWildcardPart(instance);
                wildcardString.append(':').append(instance);
            }
        }
        return subject.isPermitted(new WildcardPermission(wildcardString.toString()));
    }
    
    private static String normalizeWildcardPart(String permission) {
        if (permission == null) return "*";
        if (permission.indexOf(':') < 0) return permission;
        return permission.replace(':', '_');
    }

    public static boolean isPermitted(String wildcardString) {
        Subject subject =  M.l(ShiroSecurity.class).getSubject(); // init
        return subject.isPermitted(new WildcardPermission(wildcardString));
    }

    public static Locale getLocale() {
        return getLocale(getSubject());
    }
    
    public static Locale getLocale(Subject subject) {
        Session session = subject.getSession(false);
        if (session != null) {
            Object locale = session.getAttribute(ATTR_LOCALE);
            if (locale != null) {
                if (locale instanceof Locale)
                    return (Locale)locale;
                if (locale instanceof String)
                    return Locale.forLanguageTag((String)locale);
            }
        }
        return Locale.getDefault();
    }
    
    public static void setLocale(Locale locale) {
        setLocale(getSubject(), locale);
    }
    
    public static void setLocale(Subject subject, Locale locale) {
        Session session = subject.getSession();
        session.setAttribute(ATTR_LOCALE, locale);
    }

    public static void setLocale(Subject subject, String locale) {
        Session session = subject.getSession();
        session.setAttribute(ATTR_LOCALE, Locale.forLanguageTag(locale));
    }

    public static Object getSessionAttribute(String key) {
        Session session = getSubject().getSession(false);
        if (session == null) return null;
        Object res = session.getAttribute(key);
        if (res != null) return res;
        PrincipalData data = (PrincipalData) session.getAttribute(PrincipalData.SESSION_KEY);
        if (data != null) {
            
        }
        return null;
    }
    
    public static String getSessionAttribute(String key, String def) {
        Object ret = getSessionAttribute(key);
        if (ret == null) return def;
        if (ret instanceof String) return (String)ret;
        return String.valueOf(ret);
    }
    
    /**
     * Login and load user data from source (if needed and possible).
     * 
     * @param subject - the subject to login
     * @param user - the user
     * @param pass - the password
     * @param rememberMe 
     * @param locale The current locale for the session or null
     * @return true if login was successful
     */
    public static boolean login(Subject subject, String user, String pass, boolean rememberMe, Locale locale) {
        UsernamePasswordToken token = new UsernamePasswordToken(user, MPassword.decode(pass));
        token.setRememberMe(rememberMe);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            log.d(e);
            return false;
        }
        loadPrincipalData(subject);
        if (locale != null)
            setLocale(locale);
        return true;
    }

}