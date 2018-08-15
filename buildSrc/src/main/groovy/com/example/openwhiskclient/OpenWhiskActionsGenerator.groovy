import com.sun.codemodel.*

public class OpenWhiskActionsGenerator {

    public static void generateActions(final File outputDir) throws Exception {
        def model = new JCodeModel()

        def actionName = "testaction"

        def fqdn = OpenWhiskActionsGenerator.class.getPackage().getName() + "." + actionName
        def clazz = model._class(JMod.PUBLIC | JMod.FINAL, fqdn, ClassType.CLASS)
//        clazz._implements(c)

        def constantField = clazz.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, String.class, "ACTION_NAME", JExpr.lit(actionName))
        def getName = clazz.method(JMod.PUBLIC, String.class, "getName")
        getName.body()._return(constantField)

        if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
            throw new IOException("Could not create directory: " + outputDir)
        }

        model.build(outputDir)
    }
}
