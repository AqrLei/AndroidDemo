package open.aqrlei.com.rxjavasample.rxjava

import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author  aqrLei on 2018/7/17
 */
object TransformOperator {
    enum class TransformType {
        BUFFER_COUNT,
        BUFFER_TIME,
        FLAT_MAP,
        GROUP_BY,
        MAP, SCAN,
        WINDOW_COUNT,
        WINDOW_TIME
    }

    fun subscribe(action: (String) -> Unit) {
        Observable.rangeLong(0, 10).transform(TransformType.values()[Random().nextInt(8)],action)
    }

    private fun Observable<Long>.transform(type: TransformType,action: (String) -> Unit) {
        val result = StringBuilder()
        result.append("Transform: ")
        when (type) {
            TransformType.BUFFER_COUNT -> {
                /*收集数据到一个集合后批量发送*/
                this.buffer(2).subscribe {
                    result.append("bufferCount: $it\t")
                    action(result.toString())
                }
            }
            TransformType.BUFFER_TIME -> {
                /*收集数据到一个集合，周期性发送(可限定每次发送数据size的上限)*/
                this.buffer(1, TimeUnit.SECONDS, 5)
                        .subscribe {
                            result.append("bufferTime: $it\t")
                            action(result.toString())
                        }
            }
            TransformType.FLAT_MAP -> {
                /*将前一个Observable发射的数据经过再次的处理，输出Observable*/
                this.flatMap {
                    Observable.just(it - 1L)
                }.subscribe {
                    result.append("flatMap: $it\t")
                    action(result.toString())
                }
            }
            TransformType.GROUP_BY -> {
                /*将数据进行分组*/
                this.groupBy {
                    it % 3
                }.subscribe { observable ->
                    observable.subscribe {
                        result.append("groupBy:\nkey: ${observable.key} value:$it\t")
                        action(result.toString())
                    }
                }
            }
            TransformType.MAP -> {
                /*返回经过处理后的数据（flatMap返回的是Observable)*/
                this.map {
                    it + 1
                }.subscribe {
                    result.append("map: $it\t")
                    action(result.toString())
                }
            }
            TransformType.SCAN -> {
                /*将数据进行自定义规则的转换，t1是上次发射的数据，t2是本次发射的数据，返回处理后的结果*/
                this.scan { t1: Long, t2: Long ->
                    t2 - t1
                }.subscribe {
                    result.append("scan: $it\t")
                    action(result.toString())
                }

            }
            TransformType.WINDOW_COUNT -> {
                /*将数据收集到一个集合后，打包发射一个Observable(buffer发射的数据)*/
                this.window(2)
                        .subscribe { observable ->
                            observable.subscribe {
                                result.append("windowCount: \n observable: ${observable.hashCode()} value: $it\t")
                                action(result.toString())
                            }
                        }
            }
            TransformType.WINDOW_TIME -> {
                /*与buffer不同，window发射的是Observable*/
                window(1, TimeUnit.SECONDS, 5)
                        .subscribe { observable ->

                            observable.subscribe {
                                result.append("windowTime: \n observable: ${observable.hashCode()} value: $it\t")
                                action(result.toString())
                            }
                        }
            }
        }
    }
}