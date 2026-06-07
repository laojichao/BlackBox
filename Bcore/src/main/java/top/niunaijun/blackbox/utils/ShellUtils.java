package top.niunaijun.blackbox.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utility class for executing shell commands on the device. Supports both root
 * and non-root command execution, single and multiple commands, and captures
 * command output and exit codes. Commands are executed via {@code Runtime.exec()}.
 */
public class ShellUtils {
    /** Command to start a root shell session. */
    public static final String COMMAND_SU = "su";
    /** Command to start a standard shell session. */
    public static final String COMMAND_SH = "sh";
    /** Command to exit a shell session. */
    public static final String COMMAND_EXIT = "exit\n";
    /** Line separator used when writing commands to the shell process. */
    public static final String COMMAND_LINE_END = "\n";


    private ShellUtils() {
        throw new AssertionError();
    }


    /**
     * Checks whether the device has root access by attempting to execute
     * an {@code echo root} command with root privileges.
     *
     * @return {@code true} if the root command executed successfully (exit code 0)
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }


    /**
     * Executes a single shell command and returns the result with output messages.
     *
     * @param command the shell command to execute
     * @param isRoot whether to execute with root privileges
     * @return the {@link CommandResult} containing the exit code and output
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }


    /**
     * Executes a list of shell commands sequentially and returns the result with output messages.
     *
     * @param commands the list of shell commands to execute
     * @param isRoot whether to execute with root privileges
     * @return the {@link CommandResult} containing the exit code and output
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

    /**
     * Executes an array of shell commands sequentially and returns the result with output messages.
     *
     * @param commands the array of shell commands to execute
     * @param isRoot whether to execute with root privileges
     * @return the {@link CommandResult} containing the exit code and output
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }


    /**
     * Executes a single shell command with control over root access and output capture.
     *
     * @param command the shell command to execute
     * @param isRoot whether to execute with root privileges
     * @param isNeedResultMsg whether to capture the command's standard output
     * @return the {@link CommandResult} containing the exit code and optionally the output
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }


    /**
     * Executes a list of shell commands sequentially with control over root access and output capture.
     *
     * @param commands the list of shell commands to execute
     * @param isRoot whether to execute with root privileges
     * @param isNeedResultMsg whether to capture the commands' standard output
     * @return the {@link CommandResult} containing the exit code and optionally the output
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * Executes an array of shell commands sequentially. This is the core execution method
     * that all other overloads delegate to. Commands are written to the shell process's
     * stdin, and the exit code is captured after the process completes.
     *
     * @param commands the array of shell commands to execute; may be {@code null}
     * @param isRoot whether to execute via {@code su} (root) or {@code sh} (standard)
     * @param isNeedResultMsg whether to capture the standard output of the commands
     * @return the {@link CommandResult} containing the exit code (0 for success, -1 for error)
     *         and optionally the captured output message
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        StringBuilder successMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString());
    }


    /**
     * Represents the result of a shell command execution. Contains the process exit code
     * and optionally the captured standard output.
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {


        /** The process exit code; 0 indicates success, non-zero indicates an error. */
        public int result;
        /** The captured standard output of the command, or {@code null} if not requested. */
        public String successMsg;



        /**
         * Creates a command result with only an exit code.
         *
         * @param result the process exit code
         */
        public CommandResult(int result) {
            this.result = result;
        }


        /**
         * Creates a command result with an exit code and output message.
         *
         * @param result the process exit code
         * @param successMsg the captured standard output, or {@code null}
         */
        public CommandResult(int result, String successMsg) {
            this.result = result;
            this.successMsg = successMsg;
        }
    }
}
