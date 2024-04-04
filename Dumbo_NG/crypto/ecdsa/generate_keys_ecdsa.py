import argparse
import pickle
import os

from coincurve import PrivateKey

def generate_key_list(players):
    return [PrivateKey().secret for _ in range(players)]


def main():
    """ """
    parser = argparse.ArgumentParser()
    parser.add_argument('players', help='The number of players')
    args = parser.parse_args()
    n = int(args.players)
    sk_keylist = generate_key_list(n)

    for i in range(n):
        with open(os.path.join(f"keys-{n}", f"sSK2-{i}.key"), "wb") as fh:
            pickle.dump(sk_keylist[i], fh)

if __name__ == '__main__':
    main()
