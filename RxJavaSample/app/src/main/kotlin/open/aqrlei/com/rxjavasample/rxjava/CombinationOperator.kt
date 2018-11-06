package open.aqrlei.com.rxjavasample.rxjava

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author  aqrLei on 2018/7/17
 */
object CombinationOperator {
    enum class CombinationType {
        COMBINE_LATEST,
        JOIN,
        MERGE,
        START_WITH,
        SWITCH_ON_NEXT,
        ZIP
    }

    fun subscribe(action: (String) -> Unit) {
        combinationObservable(CombinationType.values()[Random().nextInt(6)], action)
    }

    private fun combinationObservable(type: CombinationType, action: (String) -> Unit) {
        val result = StringBuilder()
        result.append("Combination: ")
        val observable1 = Observable.intervalRange(10, 5, 100, 100, TimeUnit.MILLISECONDS)
        val observable2 = Observable.intervalRange(12, 5, 100, 200, TimeUnit.MILLISECONDS)
        when (type) {
            CombinationType.COMBINE_LATEST -> {
                /*
                * 将当前Observable最新的数据进行组合发射
                * 100 : 10,12 ->22
                * 200 : 11,12 ->23
                * 300 : 12,12 ->24
                * */
                Observable.combineLatest(
                        observable1,
                        observable2,
                        BiFunction<Long, Long, Long> { t1, t2 ->
                            result.append("combineLatest:\n observable1: $t1, observable2: $t2\t")
                            action(result.toString())
                            t1 + t2
                        })
                        .switchThread()
                        .subscribe {
                            result.append("combineLatest: $it\t")
                            action(result.toString())
                        }
            }
            CombinationType.JOIN -> {
                /*
                * 如果一个Observable发射的数据在另一个Observable的发射时间周期内，就合并发射
                * 100 10 ,12 -> 22
                * 200 11,12 ->23
                * */
                observable1.join(
                        observable2,
                        Function<Long, Observable<Long>> {
                            result.append("join:\n observable1: $it\t")
                            action(result.toString())
                            Observable.just(it).delay(100, TimeUnit.MILLISECONDS)
                        },
                        Function<Long, Observable<Long>> {
                            result.append("join:\n observable2: $it\t")
                            action(result.toString())
                            Observable.just(it).delay(100, TimeUnit.MILLISECONDS)
                        },
                        BiFunction<Long, Long, Long> { t1, t2 ->
                            result.append("join:\n observable1: $t1, observable2: $t2\t")
                            action(result.toString())
                            t1 + t2
                        })
                        .switchThread()
                        .subscribe {
                            result.append("join: $it\t")
                            action(result.toString())
                        }

            }
            CombinationType.MERGE -> {
                /*将两个Observable发射的数据按照时间顺序进行组合，转化成一个Observable发射*/
                Observable.merge(observable1, observable2)
                        .switchThread()
                        .subscribe {
                            result.append("merge: $it\t")
                            action(result.toString())
                        }

            }
            CombinationType.START_WITH -> {
                Observable.just(1, 3, 4)
                        /*在发射数据之前先发射一个数据项或数据序列*/
                        .startWith(0)
                        .switchThread()
                        .subscribe {
                            result.append("startWith: $it\t")
                            action(result.toString())
                        }
            }
            CombinationType.SWITCH_ON_NEXT -> {
                val observable = Observable.interval(100, TimeUnit.MILLISECONDS).map(Function<Long, Observable<Long>> {
                    return@Function Observable.just(it + 2).delay(300, TimeUnit.MILLISECONDS)
                })
                /*将一组Observable转化成一个Observable，如果同一时间内有多个Observable，只发射最后一个Observable*/
                Observable.switchOnNext(observable)
                        .switchThread()
                        .subscribe {
                            result.append("switchOnNext: $it\t")
                            action(result.toString())
                        }
            }
            CombinationType.ZIP -> {
                /*
                * 将多个Observable的数据项组合在一起发射，严格按照周期顺序，不能单个发射
                * 200 10 12 -> 22
                * 400 11 13 -> 24
                * */
                Observable.zip(observable1, observable2, BiFunction<Long, Long, Long> { t1, t2 ->
                    result.append("zip:\n observable1: $t1, observable2: $t2\t")
                    action(result.toString())
                    t1 + t2
                })
                        .switchThread()
                        .subscribe {
                            result.append("zip: $it\t")
                            action(result.toString())
                        }
            }
        }
    }
}