def fib_gen():
    index = 0
    n_2 = 0
    n_1 = 1
    while True:
        if index == 0:
            index += 1
            yield 0
        elif index == 1:
            index += 1
            yield 1
        else:
            n = n_1 + n_2
            n_2 = n_1
            n_1 = n
            yield n


def block_ten_dec(func):
    def wrapper():
        return [next(func) for i in range(10)]
    return wrapper


def block_dec(func):
    def wrapper(block_size):
        return [next(func) for i in range(block_size)]
    return wrapper


fib_gen = block_dec(fib_gen())
for i in range(10):
    print(fib_gen(10))

