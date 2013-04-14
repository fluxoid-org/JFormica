import numpy as np
import itertools

class PolyFit:
    """
     Polynomial fitting and evaluation
    
     Credit to Joe Kington
     http://stackoverflow.com/questions/7997152/python-3d-polynomial-surface-fit-order-dependent
     """
    def polyfit2d(self, x, y, z, order=3):
        ncols = (order + 1)**2
        G = np.zeros((x.size, ncols))
        ij = itertools.product(range(order+1), range(order+1))
        for k, (i,j) in enumerate(ij):
            G[:,k] = x**i * y**j
        m, _, _, _ = np.linalg.lstsq(G, z)
        return m
    
    def polyval2d(self, x, y, m):
        order = int(np.sqrt(len(m))) - 1
        ij = itertools.product(range(order+1), range(order+1))
        z = np.zeros_like(x)
        f = 0;
        for a, (i,j) in zip(m, ij):
            z += a * x**i * y**j
        return z
