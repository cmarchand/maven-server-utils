# maven-servers-utils

This project allows to acces to server declarations in settings.xml
files. It is made to be used from Mojos.

## Use is very simple :

In your Mojo, declare to properties, session and container. These 
properties will be injected by Maven, so no need to initialize them.
Then, create a MavenServersUtils and use it to request server
informations you need

```
public MyMojo extends AbstractMojo {

    @Component
    private MavenSession session;

    @Component
    private PlexusContainer container;

    @Parameter
    private String serverId;

    public void execute() ... {
        MavenServersUtils serverUtils = new MAvenServerUtils(session, container);
        ServerInformation serverInfo = serverUtils.getServerAuthentication(serverId);
        ...
    }
}
```


