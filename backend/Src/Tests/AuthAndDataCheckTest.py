import BluePrints.DataCheck
import BluePrints.Auth
import unittest
import sys
sys.path.append("./")
sys.path.append("./BluePrints")


class AuthAndDataCheckTest(unittest.TestCase):
    def test_hash_password(self):
        password = "JecnaJeVecna"
        hash = BluePrints.Auth.hash_password(password)
        self.assertEqual(
            BluePrints.Auth.check_password_hash(password, hash), True)

    def test_hash_password_wrong(self):
        password = "JecnaJeVecna"
        hash = BluePrints.Auth.hash_password(password)
        self.assertEqual(
            BluePrints.Auth.check_password_hash("Jej", hash), False)
        
        

    def test_validate_token(self):
        self.assertEqual(type(BluePrints.DataCheck.check_for(
            "EcF8vtL9W2KGU6Dx5du9CHb8u4IVxP", BluePrints.DataCheck.TOKEN_REGEX)), str)

    def test_validate_token_wrong(self):
        with self.assertRaises(Exception):
            BluePrints.DataCheck.check_for(
                "EcF8vtL9W2KGU6Dx5du9CHbIVxP", BluePrints.DataCheck.TOKEN_REGEX)

    def test_validate_password(self):
        self.assertEqual(type(BluePrints.DataCheck.check_for(
            "Ahoj@45699", BluePrints.DataCheck.PASSWORD_REGEX)), str)

    def test_validate_password_wrong(self):
        with self.assertRaises(Exception):
            BluePrints.DataCheck.check_for(
                "AhojJak", BluePrints.DataCheck.PASSWORD_REGEX)

    def test_validate_email(self):
        self.assertEqual(type(BluePrints.DataCheck.check_for(
            "karel@gmail.com", BluePrints.DataCheck.EMAIL_REGEX)), str)

    def test_validate_email_wrong(self):
        with self.assertRaises(Exception):
            BluePrints.DataCheck.check_for(
                "EcF8vtL9W2KGU6Dx5du9CHbIVxP", BluePrints.DataCheck.EMAIL_REGEX)

    def test_validate_username(self):
        self.assertEqual(type(BluePrints.DataCheck.check_for(
            "Karel1564", BluePrints.DataCheck.USERNAME_REGEX)), str)

    def test_validate_token_username(self):
        with self.assertRaises(Exception):
            BluePrints.DataCheck.check_for(
                "AHOj%%$%5", BluePrints.DataCheck.USERNAME_REGEX)

    def test_validate_content(self):
        self.assertEqual(type(BluePrints.DataCheck.check_for(
            "Hello World!", BluePrints.DataCheck.CONTENT_REGEX)), str)

    def test_validate_token_content(self):
        with self.assertRaises(Exception):
            BluePrints.DataCheck.check_for("EcF8vtL9W2KGU6DEcF8vtLEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxP9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPEcF8vtL9W2KGU6Dx5du9CHbIVxPx5du9CHbIVxP", BluePrints.DataCheck.CONTENT_REGEX)


if __name__ == "__main__":
    unittest.main()
