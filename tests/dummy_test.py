import unittest


class DummyTestCase(unittest.TestCase):
    def test_that_everything_is_fine(self):
        self.assertEqual(3 * 4, 12)