def add_ciao_dict(ciao_dict, word):
    ciao = ciao_word(word)
    if ciao in ciao_dict:
        ciao_dict[ciao].add(word)
    else:
        ciao_dict[ciao] = {word}


def ciao_word(word):
    res = ""
    for x in sorted(word):
        res += x
    return res


def create_dict(path):
    ciao_dict = dict()
    f = open(path, "r")
    f = f.read().split()
    for x in f:
        add_ciao_dict(ciao_dict, x)
    return ciao_dict


def replace_anagrams(ciao_dict, line):
    for x in line.split():
        if ciao_word(x) in ciao_dict:
            for it in ciao_dict[ciao_word(x)].difference({x}):
                print(it, end=" ")
                break
        else:
            print(x, end=" ")


def main():
    ciao_dict = create_dict("anagram.txt")
    replace_anagrams(ciao_dict, "string betas")


if __name__ == "__main__":
    main()
