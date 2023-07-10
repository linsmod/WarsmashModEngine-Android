package lin.threading;

import java.util.ArrayList;
import java.util.List;

public class PromiseContext {
    private String status = "pending";
    private Object data;
    private Exception error;
    private String reason;

    /**
     * @return
     */
    public Object getData() {
        return this.data;
    }

    /**
     * @return
     */
    public String getReason() {
        return null;
    }

    /**
     * @return
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * @param error
     */
    public void setError(Exception error) {
        this.error = error;
    }

    public void resolve(Object data) {
        if (this.status == "resolved") {
            System.out.println("WARN: duplicate entry PromiseContext.resolve ignored");
            return;
        }
        this.status = "resolved";
        this.data = data;
    }

    public void reject(String reason) {
        this.reject(reason, null);
    }

    public void reject(String reason, Object data) {
        if (this.status == "reject") {
            System.out.println("WARN: duplicate entry PromiseContext.reject ignored");
            return;
        }
        this.status = "rejected";
        this.reason = reason;
        this.data = data;
    }

    public void addTask(PromiseFunctionImpl promiseFunction) {
        this.tasks.add(promiseFunction);
    }

    int taskOffset = -1;

    List<PromiseFunctionImpl> tasks = new ArrayList<>();

    PromiseFunctionImpl nextTask() {
        taskOffset++;
        if (tasks.size() - 1 < taskOffset) {
            return null;
        }
        return tasks.get(taskOffset);
    }
}
