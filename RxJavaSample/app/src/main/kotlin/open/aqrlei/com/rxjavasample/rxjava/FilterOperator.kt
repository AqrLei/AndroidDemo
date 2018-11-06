package open.aqrlei.com.rxjavasample.rxjava

import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author  aqrLei on 2018/7/17
 */
object FilterOperator {
    enum class FilterType {
        DEBOUNCE,
        DISTINCT,
        ELEMENT_AT,
        FILTER,
        FIRST,
        IGNORE_ELEMENTS,
        LAST,
        SAMPLE,
        SKIP,
        SKIP_LAST,
        TAKE,
        TAKE_LAST

    }

    fun subscribe(action:(String) -> Unit) {
         filterObservable(FilterType.values()[Random().nextInt(12)],action)
    }

    private fun filterObservable(type: FilterType,action: (String) -> Unit) {
        val result = StringBuilder()
        result.append("filter: ")
        when (type) {
            FilterType.DEBOUNCE -> {
                Observable.create<Long> {
                    try {
                        for (i in 1..10) {
                            it.onNext(i.toLong())
                            Thread.sleep(i * 100.toLong())
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                        /*如果在指定的时间内没有新的发射或者调用了 onComplete则发射数据*/
                        .debounce(400L, TimeUnit.MILLISECONDS)
                        .switchThread()
                        .subscribe {
                            result.append("debounce: $it\t")
                            action(result.toString())
                        }

            }
            FilterType.DISTINCT -> {
                Observable.just(1, 1, 2, 3, 4, 5, 3)
                        /*过滤掉重复的数据*/
                        .distinct()
                        .switchThread()
                        .subscribe {
                            result.append("distinct: $it\t")
                            action(result.toString())
                        }

            }
            FilterType.ELEMENT_AT -> {
                Observable.just(1, 2, 3, 4, 5)
                        /*取特定位置的值，索引起始值为0*/
                        .elementAt(2, -1)
                        .switchThread()
                        .subscribe { t ->
                            result.append("elementAt: $t\t")
                            action(result.toString())
                        }
            }
            FilterType.FILTER -> {
                Observable.just(1, 2, 3, 4, 5)
                        /*只发射符合条件的值*/
                        .filter { it > 3 }
                        .switchThread()
                        .subscribe { result.append("filter: $it\t")
                            action(result.toString())}

            }
            FilterType.FIRST -> {
                Observable.just(1, 2, 3)
                        /*取第一个值发射，或发射默认值*/
                        .first(0)
                        .switchThread()
                        .subscribe { t -> result.append("first: $t\t")
                            action(result.toString())}
            }
            FilterType.IGNORE_ELEMENTS -> {
                Observable.just(1, 2, 3, 5)
                        /*忽略掉要发射的对象，只执行onComplete或onError*/
                        .ignoreElements()
                        .switchThread()
                        .subscribe({
                            result.append("ignoreElements: onComplete\t")
                            action(result.toString())
                        }, {
                            result.append("ignoreElements: onError\t")
                            action(result.toString())
                        })
            }
            FilterType.LAST -> {
                Observable.just(1, 2, 3)
                        /*取最后一个值发射，或默认值*/
                        .last(0)
                        .switchThread()
                        .subscribe { t -> result.append("last: $t\t")
                            action(result.toString())}
            }
            FilterType.SAMPLE -> {
                Observable.intervalRange(2, 5, 100, 100, TimeUnit.MILLISECONDS)
                        /*定期扫描Observable产生的数据，只发射最新的数据*/
                        .sample(200, TimeUnit.MILLISECONDS)
                        .switchThread()
                        .subscribe { result.append("sample: $it\t")
                            action(result.toString())}
            }
            FilterType.SKIP -> {
                Observable.just(1, 2, 3, 4, 5, 6)
                        /*跳过前面的n项不发射*/
                        .skip(2)
                        .switchThread()
                        .subscribe { result.append("skip: $it\t")
                            action(result.toString())}
            }
            FilterType.SKIP_LAST -> {
                /*跳过后面的n项不发射*/
                Observable.just(1, 2, 3, 4, 5, 6)
                        .skipLast(2)
                        .switchThread()
                        .subscribe { result.append("skipLast: $it\t")
                            action(result.toString())}
            }
            FilterType.TAKE -> {
                /*取前面的n项发射*/
                Observable.just(1, 2, 3, 4, 5, 6)
                        .take(2)
                        .switchThread()
                        .subscribe { result.append("take: $it\t")
                            action(result.toString())}
            }
            FilterType.TAKE_LAST -> {
                /*取后面的n项发射*/
                Observable.just(1, 2, 3, 4, 5, 6)
                        .takeLast(2)
                        .switchThread()
                        .subscribe { result.append("skip: $it\t")
                            action(result.toString())}
            }
        }
    }
}