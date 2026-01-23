#!/usr/bin/env python3
"""
Script per convertire chiavi pubbliche PEM in formato JWKS.
Requisiti: pip install jwcrypto
"""

import json
import os
import sys
from pathlib import Path

try:
    from jwcrypto import jwk
except ImportError:
    print("Errore: libreria jwcrypto non installata.")
    print("Installare con: pip install jwcrypto")
    sys.exit(1)


def pem_to_jwk(pem_file: str) -> dict:
    """Converte un file PEM di chiave pubblica in JWK."""
    with open(pem_file, "rb") as f:
        pem_data = f.read()

    key = jwk.JWK.from_pem(pem_data)
    return json.loads(key.export_public())


def create_jwks(jwk_list: list) -> dict:
    """Crea un JWKS (JSON Web Key Set) da una lista di JWK."""
    return {"keys": jwk_list}


def save_jwks(jwks: dict, output_file: str):
    """Salva il JWKS in un file JSON."""
    with open(output_file, "w") as f:
        json.dump(jwks, f, indent=2)
    print(f"Creato: {output_file}")


def convert_single_key(pem_file: str, output_file: str):
    """Converte una singola chiave PEM in JWKS."""
    jwk_dict = pem_to_jwk(pem_file)
    jwks = create_jwks([jwk_dict])
    save_jwks(jwks, output_file)


def main():
    # Directory contenente le chiavi PEM (parent directory)
    parent_dir = Path(__file__).parent.parent
    output_dir = Path(__file__).parent

    # Mappa delle chiavi pubbliche univoche da convertire
    # (nome_base, file_pem_relativo, tipo_chiave)
    keys_to_convert = [
        # RSA standard
        ("keyPair-test-rsa", "keyPair-test.rsa.publicKey.pem", "RSA"),

        # RSA con chiave privata cifrata PKCS1
        ("keyPair-test-rsa-pkcs1-encrypted", "keyPair-test.rsa.pkcs1_encrypted.publicKey.pem", "RSA"),

        # RSA-PSS (PS256)
        ("keyPair-test-ps256", "keyPair-test-ps256_public.pem", "RSA-PSS"),

        # RSA-PSS cifrata
        ("keyPair-test-ps256-encrypted", "keyPair-test-ps256_encrypted_public.pem", "RSA-PSS"),

        # EC P-256 (ES256)
        ("keyPair-test-es256", "keyPair-test-es256_public.pem", "EC"),

        # EC P-256 cifrata
        ("keyPair-test-es256-encrypted", "keyPair-test-es256_encrypted_public.pem", "EC"),
    ]

    print("Conversione chiavi pubbliche PEM in JWKS...")
    print(f"Directory sorgente: {parent_dir}")
    print(f"Directory output: {output_dir}")
    print("-" * 50)

    converted = 0
    errors = 0

    for name, pem_file, key_type in keys_to_convert:
        pem_path = parent_dir / pem_file
        output_path = output_dir / f"{name}.jwks"

        if not pem_path.exists():
            print(f"SKIP: {pem_file} non trovato")
            continue

        try:
            convert_single_key(str(pem_path), str(output_path))
            converted += 1
        except Exception as e:
            print(f"ERRORE: {pem_file} - {e}")
            errors += 1

    print("-" * 50)
    print(f"Conversione completata: {converted} file creati, {errors} errori")


if __name__ == "__main__":
    main()
