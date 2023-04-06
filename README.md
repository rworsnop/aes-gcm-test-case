# AES/GCM decryption test case 

This demonstrates a problem with the GCM implementation in SunJCE where we can't use it for streaming during decryption
because `Cipher.update` will just keep buffering the ciphertext until `doFinal` is called.

Bouncy Castle doesn't do this.
