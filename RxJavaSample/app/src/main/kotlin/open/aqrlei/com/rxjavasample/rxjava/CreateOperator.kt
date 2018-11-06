package open.aqrlei.com.rxjavasample.rxjava

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author  aqrLei on 2018/7/17
 *
 * create、just、from、defer、range、interval、timer、empty、never
 * 、error、(repeat、delay)
 *
 */
object CreateOperator {

    enum class GenerateType {
        TIMER,
        JUST,
        FROM,
        DEFER,
        RANGE,
        INTERVAL,
        CREATE,
        EMPTY,
        NEVER,
        ERROR,
        REPEAT,
        DELAY
    }

    fun subscribe(action: (String) -> Unit){
        val result = StringBuilder()
        result.append("Create: ")
        generateObservable(GenerateType.values()[Random().nextInt(12)]).switchThread()
                .subscribe({
                    result.append("${GenerateType.values()[it.toInt()]} \t")
                    action(result.toString())
                }, {
                    result.append("${it.message} \t")
                    action(result.toString())
                }, {
                    result.append("complete \t ")
                    action(result.toString())
                })
    }

    private fun generateObservable(type: GenerateType): Observable<Long> {
        return when (type) {
            GenerateType.CREATE -> {
                /*最基本的创建操作符*/
                Observable.create<Long> {
                    it.onNext(6)
                    it.onComplete()
                }
            }
            GenerateType.JUST -> {
                /*
                * 将某个对象转换为Observable对象并发射出去
                * 此对象在just时就确定了，如果是个变量，即使之后更新了，
                * just中还是原来的值
                * */
                Observable.just(1)
            }
            GenerateType.FROM -> {
                /*将某个对象转换为Observable对象并发射出去，如果是list对象，则会依次发射里面的item(这是与just不同的地方)*/
                Observable.fromCallable {
                    2.toLong()
                }
            }
            /*
            * 在订阅（subscribe)的时候才创建Observable对象，可以保证要发射的值是最新的
            * */
            GenerateType.DEFER -> {
                Observable.defer {
                    Observable.just(3L)
                }
            }
            /*
            * 根据传入的 初始值 start 和 发送次数 count
            * 发射 start, start+1,...start+count-1的数值
            * */
            GenerateType.RANGE -> {
                Observable.rangeLong(4, 5)
            }
            /*
            * 每个对象之间间隔多长时间发射
            * */
            GenerateType.INTERVAL -> {
                Observable.intervalRange(5, 5, 1, 1, TimeUnit.SECONDS)
            }
            /*
            * 延迟时间发射对象
            * */
            GenerateType.TIMER -> {
                Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
            }
            /*
            * 不发射任何数据，直接执行onComplete
            * */
            GenerateType.EMPTY -> {
                Observable.empty<Long>()
            }
            /*
            *无数据，无通知
            * */
            GenerateType.NEVER -> {
                Observable.never<Long>()
            }
            /*
            * 回调 onError
            * */
            GenerateType.ERROR -> {
                Observable.error<Long> {
                    Throwable("error test")
                }
            }
            /*
            * 重复发射，订阅在computation Scheduler~
            * */
            GenerateType.REPEAT -> {
                Observable.just(10L).repeat(5)
            }
            /*延迟事件中的某一次发送*/
            GenerateType.DELAY -> {
                Observable.just(11L).delay(1, TimeUnit.SECONDS)
            }
        }

    }


}