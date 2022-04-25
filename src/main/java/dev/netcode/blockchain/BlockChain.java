package dev.netcode.blockchain;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BlockChain<T> {

	@Getter private ArrayList<Block<T>> blocks;
	
	public BlockChain() {
		this.blocks = new ArrayList<Block<T>>();
	}
	
	public boolean validateChain() {
		Block<T> previousBlock = null;
		
		for(var block : blocks) {
			if(!block.validate()) {
				return false;
			}
			if(previousBlock != null && !block.getPreviousHash().equals(previousBlock.getHash())) {
				return false;
			}
			previousBlock = block;
		}
		return true;
	}
	
	public void rewindToLastValidState() {
		while(!validateChain()) {
			blocks.remove(blocks.size()-1);
		}
	}
	
	public void addBlock(Block<T> block) {
		blocks.add(block);
	}
	
	public Block<T> getBlock(int index){
		if(blocks.size() == 0) return null;
		return blocks.get(index);
	}
	
	public Block<T> getLastBlock(){
		if(blocks.size() == 0) return null;
		return blocks.get(blocks.size()-1);
	}
	
	public int getBlockCount() {
		return blocks.size();
	}
	
}
