"""
This module glues the ``translation``, ``json_preproc`` and ``network`` modules
from the ``gt`` package and the ``json`` module from the Python standard
library.
"""
import json
from gt import translation, json_preproc, network

def get_translation(*args, **kwargs):
    """
    Returns a Translation object.

    Args: same to ``network.fetch_response``.
    """
    return translation.Translation(
        json.loads(
            json_preproc.preprocess(
                network.fetch_response(*args, **kwargs))))
