package android.content.res;

/**
 * Instances of this class can be used for {@link XResources#setReplacement(String, String, String, Object)}
 * and its variants. They forward the resource request to a different {@link Resources}
 * instance with a possibly different ID.
 *
 * <p>This enables resource forwarding from one package's resources to another, which is the
 * mechanism behind resource replacement in Xposed. When a resource is requested and an
 * {@code XResForwarder} replacement is set, the framework looks up the resource in the target
 * {@link Resources} object using the target ID instead.
 *
 * <p>Usually, instances aren't created directly but via {@link XModuleResources#fwd}.
 */
public class XResForwarder {
	private final Resources res;
	private final int id;

	/**
	 * Creates a new instance.
	 *
	 * @param res The target {@link Resources} instance to forward requests to.
	 * @param id The target resource ID.
	 */
	public XResForwarder(Resources res, int id) {
		this.res = res;
		this.id = id;
	}

	/** Returns the target {@link Resources} instance. */
	public Resources getResources() {
		return res;
	}

	/** Returns the target resource ID. */
	public int getId() {
		return id;
	}
}
