package com.example.ai_smile.http;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Administrator on 2020/5/21.
 *
 */

public abstract class UseCase<Q extends UseCase.RequestValues> {

    private Scheduler executorThread;
    private Scheduler uiThread;

    private Subscription subscription = Subscriptions.empty();

    public UseCase() {
        this(JobSchedule.getInstance().getScheduler(), AndroidSchedulers.mainThread());
    }

    public UseCase(Scheduler executorThread, Scheduler uiThread) {
        this.executorThread = executorThread;
        this.uiThread = uiThread;
}

    public void execute(Subscriber UseCaseSubscriber , Q rv) {
        /**
         * 1.subscribeOn的调用切换之前的线程。
           2.observeOn的调用切换之后的线程。
           3.observeOn之后，不可再调用subscribeOn 切换线程
         */
        this.subscription = this.buildUseCaseObservable(rv)
                .subscribeOn(executorThread)
                .observeOn(uiThread)
                .subscribe(UseCaseSubscriber);
    }



    public void unSubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    protected abstract Observable buildUseCaseObservable(Q rv);

    public interface RequestValues {
        boolean checkInput();
        int getErrorStringRes();
    }

    public interface RequestPageValue{
        void setPage(int page);
        void setLimit(int limit);
        int getPage();
        int getLimit();
    }

}
