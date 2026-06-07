package black.android.os;

import java.io.FileDescriptor;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.os.MemoryFile.
 * Provides access to the underlying file descriptor of an ashmem region.
 */
@BClassName("android.os.MemoryFile")
public interface MemoryFile {
    /**
     * Get the FileDescriptor of the underlying shared memory region.
     */
    @BMethod
    FileDescriptor getFileDescriptor();
}
