package de.mhus.lib.core.crypt;

import de.mhus.lib.core.MMath;

/**
 * TODO Not Working !!!
 * rotate left encode and right for decode current block value.
 * @author mikehummel
 *
 */
public class CipherBlockRotate implements CipherBlock {
	
	private byte[] block;
	private int pos;

	public CipherBlockRotate(int size) {
		block = new byte[size]; 
	}
	
	public byte[] getBlock() {
		return block;
	}
	
	public int getSize() {
		return block.length;
	}

	@Override
	public void reset() {
		pos = 0;
	}

	@Override
	public byte encode(byte in) {
		in = MMath.rotl(in, (block[pos] + 128) % 8);
		next();
		return in;
	}

	@Override
	public byte decode(byte in) {
		in = MMath.rotr(in, (block[pos] + 128) % 8);
		next();
		return in;
	}

	private void next() {
		pos = (pos + 1) % block.length;
	}
	
}
