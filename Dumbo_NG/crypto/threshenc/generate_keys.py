# from crypto.threshenc.tpke import dealer, serialize
from crypto.threshenc.tpke import dealer, serialize
import argparse
import pickle
import os


def _generate_keys(players, k):
    if k:
        k = int(k)+1
    else:
        k = players // 2  # N - 2 * t
    PK, SKs = dealer(players=players, k=k)
    return (PK.l, PK.k, serialize(PK.VK), [serialize(VKp) for VKp in PK.VKs],
            [(SK.i, serialize(SK.SK)) for SK in SKs])


def main():
    # TODO: update to output to the correct files
    """ """
    parser = argparse.ArgumentParser()
    parser.add_argument('players', help='The number of players')
    parser.add_argument('k', help='k')
    args = parser.parse_args()
    n = int(args.players)
    keys = _generate_keys(n, args.k)

    with open(os.path.join(f"keys-{n}", "ePK.key"), "wb") as fh:
        pickle.dump(keys[2], fh)

    for i in range(n):
        with open(os.path.join(f"keys-{n}", f"eSK-{i}.key"), "wb") as fh:
            pickle.dump(keys, fh)

if __name__ == '__main__':
    main()
