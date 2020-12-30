import os

from tabulate import tabulate
import time
import threading


def benchmark(func, warmups=0, iter=1, verbose=False, csv_file=None):
    def print_table(avg, var, iterations):
        res = "{}".format(tabulate([[avg, var]], headers=['AVG', 'Variance']))
        if verbose:
            res = "{}\n{}\n".format(res, (tabulate([[i, it[1], it[0]] for i, it in enumerate(iterations)],
                                                   headers=['run num', 'is warmup', 'timing'])))
        print(res)

    def csv(iterations):
        with open(csv_file, "w+", newline='') as f:
            f.write("run num, is warmup, timing\n")
            for i, it in enumerate(iterations):
                f.write("{}\n".format(", ".join([str(i), it[1], str(it[0])])))

    def running_time(*args, **kwargs):
        start = time.perf_counter()
        func(*args, **kwargs)
        end = time.perf_counter()
        return end - start

    def wrapper(*args, **kwargs):
        wup = []
        exe = []
        avg = 0
        for i in range(warmups):
            wup.append((running_time(*args, **kwargs), "yes"))
        for i in range(iter):
            rt = running_time(*args, **kwargs)
            avg += rt
            exe.append((rt, "no"))
        avg /= iter
        var = 0
        for (ns, tp) in exe:
            var += (ns - avg) * (ns - avg)
        var /= iter
        wup.extend(exe)
        print_table(avg, var, wup)
        if csv_file is not None:
            csv(wup)

    return wrapper


class MyThread(threading.Thread):
    def __init__(self, fun, n_times, *args, **kwargs):
        super().__init__()
        self.fun = fun
        self.n_times = n_times
        self.args = args
        self.kwargs = kwargs

    def run(self):
        for i in range(self.n_times):
            self.fun(*self.args, **self.kwargs)


def test(fun, *args, **kwargs):
    def multi(fun, n_threads, n_times, *args, **kwargs):
        t_list = []
        for i in range(n_threads):
            t_list.append(MyThread(fun, n_times, *args, **kwargs))

        def run():
            print("\n\nStarting test with: {} thread\n".format(n_threads))
            for i in t_list:
                i.start()
            for i in t_list:
                i.join()

        return benchmark(run, warmups=0,
                         iter=1,
                         verbose=False,
                         csv_file="f_" + str(n_threads) + "-" + str(n_times))

    multi(fun, 1, 16, *args, **kwargs)()
    multi(fun, 2, 8, *args, **kwargs)()
    multi(fun, 4, 4, *args, **kwargs)()
    multi(fun, 8, 2, *args, **kwargs)()
    """single = benchmark(fun, iter=16, verbose=True, csv_file="f_1_16.csv")
    single(*args, **kwargs)
    eight_2 = multi(benchmark(fun, iter=8, verbose=True, csv_file="f_2_8.csv"), 2, *args, **kwargs)
    four_4 = multi(benchmark(fun, iter=4, verbose=True, csv_file="f_4_4.csv"), 4, *args, **kwargs)
    two_8 = multi(benchmark(fun, iter=2, verbose=True, csv_file="f_8_2.csv"), 8, *args, **kwargs)
    eight_2()
    four_4()
    two_8()"""


def fib(n):
    if n == 0:
        return 0
    elif n == 1:
        return 1
    return fib(n - 1) + fib(n - 2)


test(fib, 2)
